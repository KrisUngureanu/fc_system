const path = require('path');

module.exports = {
  entry: './index.js',
  mode: 'development',
  output: {
    filename: 'or3-module.js',
    path: path.resolve(__dirname, '.'),
    library: 'or3web'
  },
  devtool: 'source-map'
};