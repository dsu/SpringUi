package pl.springui.components.form;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
	public static final Message ERROR = new Message("ERROR", MessageType.error);
	public static final Message OK = new Message("OK", MessageType.success);
	public static Message error(String msg) {
		return new Message(msg, MessageType.error);
	}
	public static Message ok(String msg) {
		return new Message(msg, MessageType.success);
	}
	public static Message warn(String msg) {
		return new Message(msg, MessageType.warning);
	}
	protected String description;

	protected boolean dialog = false;

	protected String message;

	protected MessageType type;

	public Message(String message, MessageType messageType) {
		super();
		this.message = message;
		this.type = messageType;
	}

	public Message(String message, MessageType messageType, boolean dialog) {
		super();
		this.dialog = dialog;
		this.message = message;
		this.type = messageType;
	}

	public Message(String message, String description, MessageType messageType) {
		super();
		this.description = description;
		this.dialog = true;
		this.message = message;
		this.type = messageType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
      return true;
    }
		if (obj == null) {
      return false;
    }
		if (getClass() != obj.getClass()) {
      return false;
    }
		Message other = (Message) obj;
		if (message == null) {
			if (other.message != null) {
        return false;
      }
		} else if (!message.equals(other.message)) {
      return false;
    }
		if (type != other.type) {
      return false;
    }
		return true;
	}

	public String getDescription() {
		return description;
	}

	public String getMessage() {
		return message;
	}

	public MessageType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public boolean isDialog() {
		return dialog;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDialog(boolean dialog) {
		this.dialog = dialog;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setType(MessageType messageType) {
		this.type = messageType;
	}

	public String toJson() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", getMessage());
		jsonObject.put("is_dialog", isDialog());
		jsonObject.put("type", getType().toString());
		return jsonObject.toString();
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", type=" + type + "]";
	}

}
