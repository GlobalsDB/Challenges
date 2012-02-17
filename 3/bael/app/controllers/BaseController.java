package controllers;

import play.mvc.Controller;
import play.mvc.Http.StatusCode;

public class BaseController extends Controller {

	protected static void internalError()
	{
		response.status = StatusCode.INTERNAL_ERROR;
	}
	
	protected static void notFound()
	{
		response.status = StatusCode.NOT_FOUND;
	}
	
	protected static void ok()
	{
		response.status = StatusCode.OK;
	}
	
}
