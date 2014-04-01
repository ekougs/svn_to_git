package com.bisam.svntogit;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class TagsCreatorMain {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        long start = new Date().getTime();
        Main.createTags(args[0]);
        Main.logStep(start, "Tags: ");
    }
}
