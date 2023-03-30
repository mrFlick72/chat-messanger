var path = require('path');

const BUID_DIR = path.resolve(__dirname + "../../../../target/classes/static");


module.exports = {
    mode: 'development',
    entry: {
        app: path.resolve(__dirname, './src/app/index.tsx'),
        login: path.resolve(__dirname, './src/login/index.tsx'),
    },
    resolve: {
        extensions: ['.tsx', '.ts', ".js", ".jsx"]
    },
    devtool: "inline-source-map", // set to none for production

    plugins: [],
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: ['ts-loader'],
                exclude: /node_modules$/,
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: path.join(__dirname, "."),
                exclude: path.resolve(__dirname, "node_modules"),
                use: {
                    loader: "babel-loader",
                    options: {
                        presets: ['@babel/env', '@babel/react']
                    }
                }

            }
        ]
    },
    output: {
        filename: 'asset/[name]_bundle.js',
        publicPath: "/",
        path: BUID_DIR
    }
};