package com.rising.insta.src.alarm;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponse;
import com.rising.insta.src.alarm.dto.GetAlarmResp;
import com.rising.insta.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/alarm")
public class AlarmController {
    private final AlarmService alarmService;
    private final JwtService jwtService;

    @GetMapping()
    public BaseResponse<?> alarmList() {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        List<GetAlarmResp> getAlarmRespList = null;
        try {
            getAlarmRespList = alarmService.readAllAlarm(idx);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        return new BaseResponse<>(getAlarmRespList);
    }

}
