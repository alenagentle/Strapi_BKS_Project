package ru.bcs.creditmarkt.strapi.utils;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileQueue {
    public static BlockingQueue<Path> references = new LinkedBlockingQueue<>();
}
