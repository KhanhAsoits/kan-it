package com.example.kan_it.DTO;

import com.example.kan_it.model.Post;
import com.example.kan_it.model.User;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PostDTO extends Post {
    public String user_photo;
    public int star;
    public String user_name;
    public UserDTO user;

    public String getDurationTimeRead(long content_length) {
        int count_word_per_minute = 200;
        int minute_per_hours = 60;
        int minute = (int) Math.ceil((double) content_length / count_word_per_minute);
        int hours = minute / minute_per_hours;
        int minutes = minute % minute_per_hours;
        if (hours > 0) {
            return "Khoảng " + hours + "giờ " + minutes + " phút đọc";
        }
        if (minutes > 0) {
            return "Khoảng " + minutes + " phút đọc";
        }
        return "Câu hỏi";
    }

    public String getTimeUp(long updated_at) {
        Date pastDate = new Date(updated_at);
        long currentTime = System.currentTimeMillis();
        long pastTime = pastDate.getTime();
        long timeDiff = currentTime - pastTime;

        long day = TimeUnit.MILLISECONDS.toDays(timeDiff);
        if (day > 0) {
            return pastDate.toLocaleString();
        }
        long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
        if (hours > 0) {
            return "Khoảng " + hours + " giờ trước";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        if (minutes > 0) {
            return "Khoảng " + minutes + " phút trước";
        }

        return "Vừa đăng";
    }
}
