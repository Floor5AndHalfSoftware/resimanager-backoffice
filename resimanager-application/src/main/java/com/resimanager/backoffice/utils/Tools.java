package com.resimanager.backoffice.utils;

public class Tools {
    public static boolean isValidPagination(int page, int size) {
        return page >= 0 && size > 0;
    }
}
