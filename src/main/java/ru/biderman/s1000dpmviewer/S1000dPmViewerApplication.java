package ru.biderman.s1000dpmviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class S1000dPmViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(S1000dPmViewerApplication.class, args);
    }

}

// TODO dto пользователей ?
// TODO не забыть про csrf

// TODO дополнительное поле: кто добавил публикацию

// TODO разобраться, нельзя ли сделать в Publication поле типа XML (Document)
// TODO попробовать отделить интеграционные тесты на postgres?