module.exports = {
  presets: ['module:@react-native/babel-preset'],
  plugins: [
    ["dotenv-import", {
      "moduleName": "@env",
      "path": ".env.local",
      "blocklist": null,
      "allowlist": null,
      "safe": false,
      "allowUndefined": true
    }],
    [
      'module-resolver', 
      {
        extensions: ['.ios.js', '.android.js', '.ios.jsx', '.android.jsx', '.js', '.jsx', '.json', '.ts', '.tsx'],
        root: ['.'],
        alias: {
          '@src': './src',
          '@api': './src/api',
          '@helper': './src/helper',
          '@model': './src/model',
          '@view': './src/view',
          '@page': './src/page',
          '@store': './src/store',
          '@hook': './src/hook',
          '@asset': './src/asset',
        },
      },
    ],
  ],
};
