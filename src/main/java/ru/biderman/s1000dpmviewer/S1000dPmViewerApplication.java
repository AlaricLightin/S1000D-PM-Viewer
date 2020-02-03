package ru.biderman.s1000dpmviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class S1000dPmViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(S1000dPmViewerApplication.class, args);
    }

}
// TODO пройти по всем satisfy в тестах и посмотреть, нельзя ли от них избавиться с помощью extracting (или чего-то ещё)
// TODO обработка ошибок во фронтенде (+ отвалился сервер)
// TODO security
// TODO не забыть про csrf
// TODO разобраться, нельзя ли сделать в Publication поле типа XML (Document)
// TODO попробовать отделить интеграционные тесты на postgres?