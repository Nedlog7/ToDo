/**
 * Copyright 2004-2013 Crypto-Pro. All rights reserved.
 * Программный код, содержащийся в этом файле, предназначен
 * для целей обучения. Может быть скопирован или модифицирован
 * при условии сохранения абзацев с указанием авторства и прав.
 *
 * Данный код не может быть непосредственно использован
 * для защиты информации. Компания Крипто-Про не несет никакой
 * ответственности за функционирование этого кода.
 */
package ru.yandex.todo.util;

/**
 * Служебный класс Constants с перечислением глобальных
 * констант клиентского приложения.
 *
 */
public interface Constants {

    String TAG = "ToDo";

    int NOTIFICATION_ID = 1;
    String NOTIFICATION_WORK = "notification_work";
    String SYNC_WORK = "sync_work";

    String token = "37972e567f5940bd84b0fe97d315eb7c";

    String yandexUrl = "https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net/";

    int cacheSize = 10 * 1024 * 1024; // this is 10MB

}
