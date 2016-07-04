package com.caitu99.gateway.gateway.model;

public class ApiResponse {

	private int code;
	private String message;
	private String data;

	public ApiResponse() {

	}

	public ApiResponse(int code, String data) {
		this.code = code;
		this.message = "";
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String description) {
		this.message = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{")
                .append("code:") .append(code)
                .append(", message:\"") .append(message).append("\"");

		if (null == data)
			sb.append(", data:\"").append("").append("\"");
		else
			sb.append(", data:\"").append(data).append("\"");

		sb.append("}");

		return sb.toString();
	}
}
