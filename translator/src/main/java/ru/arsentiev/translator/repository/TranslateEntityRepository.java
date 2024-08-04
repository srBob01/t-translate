package ru.arsentiev.translator.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.arsentiev.translator.entity.TranslateEntity;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TranslateEntityRepository {
    private final DataSource dataSource;
    private static final String SQL_INSERT = "INSERT INTO translation_logs (ip_address, input_text, translated_text," +
                                             " input_lang, translated_lang, timestamp) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    private static final String SQL_FIND_BY_ID = "SELECT id, ip_address, input_text, translated_text, input_lang, " +
                                                 "translated_lang, timestamp FROM translation_logs WHERE id = ?";
    private static final String SQL_FIND_ALL = "SELECT id, ip_address, input_text, translated_text, input_lang, " +
                                               "translated_lang, timestamp FROM translation_logs";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM translation_logs WHERE id = ?";
    private static final String SQL_UPDATE = "UPDATE translation_logs SET ip_address = ?, input_text = ?, " +
                                             "translated_text = ?, input_lang = ?, translated_lang = ?, timestamp = ? WHERE id = ?";

    public TranslateEntity save(TranslateEntity translate) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            entitySet(translate, stmt);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                translate.setId(rs.getLong("id"));
            }
            return translate;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving translation log", e);
        }
    }

    public TranslateEntity findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToTranslateEntity(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding translation log by id", e);
        }
    }

    public List<TranslateEntity> findAll() {
        List<TranslateEntity> translations = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                translations.add(mapRowToTranslateEntity(rs));
            }
            return translations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all translation logs", e);
        }
    }

    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_ID)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting translation log", e);
        }
    }

    public boolean update(TranslateEntity translate) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            entitySet(translate, stmt);
            stmt.setLong(7, translate.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating translation log", e);
        }
    }

    private void entitySet(TranslateEntity translate, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, translate.getIpAddress());
        stmt.setString(2, translate.getInputText());
        stmt.setString(3, translate.getTranslatedText());
        stmt.setString(4, translate.getInputLang());
        stmt.setString(5, translate.getTranslatedLang());
        stmt.setTimestamp(6, Timestamp.valueOf(translate.getTimestamp()));
    }

    private TranslateEntity mapRowToTranslateEntity(ResultSet rs) throws SQLException {
        TranslateEntity entity = new TranslateEntity();
        entity.setId(rs.getLong("id"));
        entity.setIpAddress(rs.getString("ip_address"));
        entity.setInputText(rs.getString("input_text"));
        entity.setTranslatedText(rs.getString("translated_text"));
        entity.setInputLang(rs.getString("input_lang"));
        entity.setTranslatedLang(rs.getString("translated_lang"));
        entity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        return entity;
    }
}
