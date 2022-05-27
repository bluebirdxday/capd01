package com.project.jjbus;

import java.util.ArrayList;

public class GlobalVariable {

    public static ArrayList<StationItem> startStations;     // 출발정류장 array
    public static ArrayList<StationItem> arrivalStations;   // 도착정류장 array

    public static ArrayList<StopBusItem> findStopBusItems;  // 도착지를 경유하는 노선 array
}
