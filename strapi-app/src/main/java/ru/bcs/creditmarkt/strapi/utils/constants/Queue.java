package ru.bcs.creditmarkt.strapi.utils.constants;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Queue {
    public static BlockingQueue<Path> fileReferencesQueue = new LinkedBlockingDeque<>();
}
