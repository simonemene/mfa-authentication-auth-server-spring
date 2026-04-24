package com.example.demo.baseconfig;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class NoWebContextConfig extends BaseConfig {
}
