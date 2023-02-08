package com.rising.insta.src.alarm.util;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.src.alarm.entity.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaveAlarm {
    private final AlarmRepository alarmRepository;

    public void save(int sender, int receiver, int type, int id) throws BaseException {
        try {
            alarmRepository.save(sender, receiver, type, id);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
