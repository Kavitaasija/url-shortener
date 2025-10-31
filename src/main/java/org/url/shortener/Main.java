package org.url.shortener;

import org.url.shortener.repository.DefaultRepository;
import org.url.shortener.service.URLService;
import org.url.shortener.service.UniqueKeyGenerator;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    System.out.println("Hello world!");

    URLService urlService = new URLService(
        new DefaultRepository(),
        new UniqueKeyGenerator());

    int index = 1;
    while (true) {
      String url1 = urlService.save("https://www.google.com/" + index);
      System.out.println(url1);
      System.out.println(urlService.get(url1));
      if (index % 10 == 0) {
        Thread.sleep(5000);
      }
      index++;
    }
  }
}