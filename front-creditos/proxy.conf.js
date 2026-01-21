const PROXY_CONF = {
  "/api": {
    "target":
      `http://${process.env.BACKEND_HOST || 'localhost'}:${process.env.BACKEND_PORT || '8080'}`, "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "secure": false,
    "pathRewrite": {
      "^/api": "/api"
    }
  }
}

module.exports = PROXY_CONF;