# react-native-google-one-tap

Google one tap sign in for react native. Works only on Android.

## Installation

```sh
npm install react-native-google-one-tap
```

## Usage

```js
import { GoogleOneTap } from "react-native-google-one-tap";

// ...

GoogleOneTap.configure({
  webClientId: '<your web client ID>',
});
const userInfo = await GoogleOneTap.signIn();
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
