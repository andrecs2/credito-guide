package com.andrecs2.credito_guide.infra.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalErrorResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	private int status;
	private String error;
	private String message;
	private String detail;
	private String path;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<String> errors;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String traceId;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String requestId;

	public static class Builder {

		private LocalDateTime timestamp;
		private int status;
		private String error;
		private String message;
		private List<String> errors;
		private String detail;
		private String path;

		public Builder timestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public GlobalErrorResponse build() {
			GlobalErrorResponse bean = new GlobalErrorResponse();
			bean.setTimestamp(this.timestamp);
			bean.setStatus(this.status);
			bean.setError(this.error);
			bean.setMessage(this.message);
			bean.setErrors(this.errors);
			bean.setDetail(this.detail);
			bean.setPath(this.path);
			return bean;
		}

		public Builder status(int status) {
			this.status = status;
			return this;
		}

		public Builder error(String error) {
			this.error = error;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Builder errors(List<String> errors) {
			this.errors = errors;
			return this;
		}

		public Builder detail(String detail) {
			this.detail = detail;
			return this;
		}

		public Builder path(String path) {
			this.path = path;
			return this;
		}

	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}