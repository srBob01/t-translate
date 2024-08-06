package ru.arsentiev.translator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.arsentiev.translator.dto.ResponseList;
import ru.arsentiev.translator.dto.TranslateRequestDto;
import ru.arsentiev.translator.dto.TranslateResponseDto;
import ru.arsentiev.translator.entity.Response;
import ru.arsentiev.translator.handler.AccessDeniedException;
import ru.arsentiev.translator.handler.LanguageNotFoundException;
import ru.arsentiev.translator.handler.TranslationException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TranslationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TranslateEntityService translateEntityService;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private TranslationService translationService;

    @Mock
    private Future<String> mockFuture; // Mock the Future object

    @BeforeEach
    public void setup() throws Exception {
        // Mock the behavior of the Future object
        when(mockFuture.get()).thenReturn("Привет");
        when(mockFuture.get(anyLong(), any(TimeUnit.class))).thenReturn("Привет");
    }

    @Test
    public void testTranslateSuccess() throws Exception {
        // Arrange
        TranslateRequestDto request = TranslateRequestDto.builder()
                .inputText("Hello")
                .inputLang("en")
                .translatedLang("ru")
                .build();
        String clientIp = "127.0.0.1";

        ResponseList mockResponse = new ResponseList();
        mockResponse.setTranslations(List.of(new Response("Привет")));

        when(restTemplate.postForObject(any(String.class), any(), eq(ResponseList.class))).thenReturn(mockResponse);

        // Mock the behavior of the ExecutorService
        when(executorService.submit(any(Callable.class))).thenReturn(mockFuture);

        // Prepare TranslateResponseDto for returning from translateEntityService.save
        TranslateResponseDto expectedResponse = TranslateResponseDto.builder()
                .id(1L) // Assumed value
                .ipAddress(clientIp)
                .inputText(request.getInputText())
                .translatedText(mockResponse.getTranslations().get(0).getText())
                .inputLang(request.getInputLang())
                .translatedLang(request.getTranslatedLang())
                .build();

        when(translateEntityService.save(any(), any(), any())).thenReturn(expectedResponse);

        // Act
        TranslateResponseDto response = translationService.translate(request, clientIp);

        // Assert
        assertNotNull(response);
        assertEquals("Привет", response.getTranslatedText());
        verify(translateEntityService, times(1)).save(any(), any(), any());
    }

    @Test
    public void testTranslateInvalidSourceLanguage() {
        // Arrange
        TranslateRequestDto request = TranslateRequestDto.builder()
                .inputText("Hello")
                .inputLang("invalid")
                .translatedLang("ru")
                .build();
        String clientIp = "127.0.0.1";

        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", "{\"code\":400,\"message\":\"Invalid source language code\"}".getBytes(), null);
        when(restTemplate.postForObject(any(String.class), any(), eq(ResponseList.class))).thenThrow(exception);

        // Act & Assert
        assertThrows(LanguageNotFoundException.class, () -> translationService.translate(request, clientIp));
    }

    @Test
    public void testTranslateUnauthorized() {
        // Arrange
        TranslateRequestDto request = TranslateRequestDto.builder()
                .inputText("Hello")
                .inputLang("en")
                .translatedLang("ru")
                .build();
        String clientIp = "127.0.0.1";

        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "", "".getBytes(), null);
        when(restTemplate.postForObject(any(String.class), any(), eq(ResponseList.class))).thenThrow(exception);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> translationService.translate(request, clientIp));
    }

    @Test
    public void testTranslateHttpException() {
        // Arrange
        TranslateRequestDto request = TranslateRequestDto.builder()
                .inputText("Hello")
                .inputLang("en")
                .translatedLang("ru")
                .build();
        String clientIp = "127.0.0.1";

        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "", "".getBytes(), null);
        when(restTemplate.postForObject(any(String.class), any(), eq(ResponseList.class))).thenThrow(exception);

        // Act & Assert
        assertThrows(TranslationException.class, () -> translationService.translate(request, clientIp));
    }
}
