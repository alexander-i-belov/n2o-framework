const proxy = require("http-proxy-middleware");

module.exports = function(app) {
  app.use(
    proxy("/n2o", {
      target: "http://docker.one:30914/sandbox/view/vWiRo/",
      changeOrigin: true
    })
  );
};
