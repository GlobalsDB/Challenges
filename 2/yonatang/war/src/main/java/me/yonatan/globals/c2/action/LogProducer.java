package me.yonatan.globals.c2.action;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

public class LogProducer {

	@Produces
	Logger logProducer(InjectionPoint ip){
		Class<?> clazz=ip.getMember().getDeclaringClass();
		return Logger.getLogger(clazz);
	}
}
