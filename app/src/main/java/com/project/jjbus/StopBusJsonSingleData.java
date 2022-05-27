package com.project.jjbus;

public class StopBusJsonSingleData {

    public Result RFC30;

    class Result {
        public JsonField code;                  // 결과코드
        public JsonField msg;                   // 결과메시지

        public RouteList routeList;             // 실시간 노선(버스)
    }

    class RouteList {
        public StopBusItem list;                // 실시간 노선(버스)
    }
}
