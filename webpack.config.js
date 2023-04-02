const path = require('path');

module.exports = {
  entry: './target/index.js',
  output: {
    filename: 'libs.js',
    path: path.resolve(__dirname, 'target/dist')
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /(node_modules)/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env']
          }
        }
      }
    ]
  }
};
