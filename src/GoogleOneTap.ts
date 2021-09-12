import { NativeModules, Platform } from 'react-native';
import type { IGoogleOneTap } from './models/IGoogleOneTap';
import type { IConfigureParams } from './models/IConfigureParams';

const { RNGoogleOneTap } = NativeModules;

const IS_IOS = Platform.OS === 'ios';

class CGoogleOneTap implements IGoogleOneTap {
  configPromise: any;

  constructor() {
    if (__DEV__ && !IS_IOS && !RNGoogleOneTap) {
      console.error(
        `RN GoogleOneTap native module is not correctly linked. Please read the readme, setup and troubleshooting instructions carefully or try manual linking. If you're using Expo, please use expo-google-sign-in. This is because Expo does not support custom native modules.`
      );
    }
  }

  async signIn() {
    if (IS_IOS) {
      return true;
    } else {
      await this.configPromise;
      return await RNGoogleOneTap.signIn();
    }
  }

  /**
   * Check if the device has Google Play Services installed. Always resolves
   * true on iOS
   */
  // async hasPlayServices(params?: IHasPlayServicesParams): Promise<boolean> {
  //   if (!params) {
  //     params = { showPlayServicesUpdateDialog: true };
  //   }
  //   if (IS_IOS) {
  //     return true;
  //   } else {
  //     if (params && params.showPlayServicesUpdateDialog === undefined) {
  //       throw new Error(
  //         'RNGoogleOneTap: Missing property `showPlayServicesUpdateDialog` in options object for `hasPlayServices`'
  //       );
  //     }
  //     return RNGoogleOneTap.playServicesAvailable(
  //       params.showPlayServicesUpdateDialog
  //     );
  //   }
  // }

  /**
   * Configures the library for login. MUST be called before attempting login
   */
  configure(params?: IConfigureParams): void {
    if (!params) {
      params = {};
    }
    if (params.offlineAccess && !params.webClientId) {
      throw new Error(
        'RNGoogleOneTap: offline use requires server web ClientID'
      );
    }
    this.configPromise = RNGoogleOneTap.configure(params);
  }

  /**
   * Returns a Promise that resolves with the current signed in user or rejects
   * if not signed in.
   */
  // async signInSilently(): User {
  //   if (IS_IOS) {
  //     return true;
  //   } else {
  //     await this.configPromise;
  //     return RNGoogleOneTap.signInSilently();
  //   }
  // }

  // async savePassword(userId, password) {
  //   if (IS_IOS) {
  //     return true;
  //   } else {
  //     await this.configPromise;
  //     return RNGoogleOneTap.savePassword(userId, password);
  //   }
  // }

  // async deletePassword(userId, password) {
  //   if (IS_IOS) {
  //     return true;
  //   } else {
  //     await this.configPromise;
  //     return RNGoogleOneTap.deletePassword(userId, password);
  //   }
  // }

  async signOut() {
    if (IS_IOS) {
      return true;
    } else {
      await this.configPromise;
      return RNGoogleOneTap.signOut();
    }
  }
}

export const GoogleOneTap = new CGoogleOneTap();
