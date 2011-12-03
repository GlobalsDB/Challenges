package com.uosipa.globalsdb.web;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Nocturne uses Google Guice as IoC container.
 * This class contains IoC setup.
 * See http://code.google.com/p/google-guice/wiki/GettingStarted
 */
public class ApplicationModule implements Module {
    @Override
    public void configure(Binder binder) {
        // No IoC binding.
    }
}
