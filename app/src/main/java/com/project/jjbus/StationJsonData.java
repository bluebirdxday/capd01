package com.project.jjbus;

import java.util.ArrayList;

public class StationJsonData {

    public Result RFC30;

    class Result {
        public JsonField code;                  // 결과코드
        public JsonField msg;                   // 결과메시지

        public RouteList routeList;             // 정류장 array
    }

    class RouteList {
        public ArrayList<StationItem> list;     // 정류장 array
    }
}
