package com.rising.insta.src.alarm.entity;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class AlarmRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void serDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Alarm> readAllAlarmByReceiverId(int receiverId) throws BaseException {
        String query = "SELECT * FROM Alarm WHERE receiver_id = ?";
        List<Alarm> alarmEntityList = null;

        try {
            alarmEntityList = this.jdbcTemplate.query(query, (rs, count) -> new Alarm(
                    rs.getInt("alarm_id"),
                    rs.getInt("sender_id"),
                    rs.getInt("receiver_id"),
                    rs.getInt("type"),
                    rs.getInt("id"),
                    rs.getString("createdAt")
            ), receiverId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return alarmEntityList;
    }

    public void save(int sender, int receiver, int type, int id) throws BaseException {
        String query = "INSERT INTO Alarm (sender_id, receiver_id, type, id) VALUES (?, ?, ?, ?)";
        Object[] params = new Object[]{sender, receiver, type, id};
        try {
            this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
