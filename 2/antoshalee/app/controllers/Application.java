package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Scope.Session;

import java.util.*;

import net.spy.memcached.ops.ConcatenationType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import core.DataFinder;
import core.DataFinder.ConditionTypes;

import models.*;

public class Application extends Controller {

	public static void index() {
		render();
	}

}