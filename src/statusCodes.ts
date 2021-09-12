import { NativeModules, Platform } from 'react-native';

const { RNGoogleOneTap } = NativeModules;

const IS_IOS = Platform.OS === 'ios';

export const statusCodes = IS_IOS
  ? {}
  : {
      SIGN_IN_CANCELLED: RNGoogleOneTap.SIGN_IN_CANCELLED as string,
      IN_PROGRESS: RNGoogleOneTap.IN_PROGRESS as string,
      PLAY_SERVICES_NOT_AVAILABLE:
        RNGoogleOneTap.PLAY_SERVICES_NOT_AVAILABLE as string,
      SIGN_IN_REQUIRED: RNGoogleOneTap.SIGN_IN_REQUIRED as string,
    };
