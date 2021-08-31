import { NativeModules } from 'react-native';

type GoogleOneTapType = {
  multiply(a: number, b: number): Promise<number>;
};

const { GoogleOneTap } = NativeModules;

export default GoogleOneTap as GoogleOneTapType;
