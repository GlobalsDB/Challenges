package models;

import core.Index;
import core.Persistent;

public class ClickStreamEvent extends Persistent {
	@Index(IndexName = "eventTypeIndex")
	public String eventType;

	@Index(IndexName = "elementTypeIndex")
	public String elementType;

	@Index(IndexName = "mouseXIndex")
	public String mouseX;

	@Index(IndexName = "mouseYIndex")
	public String mouseY;

	@Index(IndexName = "elementIdIndex")
	public String elementId;

	@Index(IndexName = "elementClassIndex")
	public String elementClass;

	@Index(IndexName = "timeStampIndex")
	public String timeStamp;

	// In SessionId IP address will be written.
	@Index(IndexName = "sessionIdIndex")
	public String sessionId;

}
