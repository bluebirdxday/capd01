package com.project.jjbus;

import java.util.ArrayList;

public class BusJsonData {

    public Result RFC30;

    class Result {
        public JsonField code;                  // 결과코드
        public JsonField msg;                   // 결과메시지

        public RouteList routeList;             // 노선(버스) array
    }

    class RouteList {
        public ArrayList<BusItem> list;         // 노선(버스) array
    }
}
