export interface IConfigureParams {
  /**
   * The Google API scopes to request access to. Default is email and profile.
   */
  scopes?: string[];

  /**
   * Web client ID from Developer Console. Required for offline access
   */
  webClientId?: string;

  /**
   * If you want to specify the client ID of type iOS
   */
  iosClientId?: string;

  /**
   * If you want to specify a different bundle path name for the GoogleService-Info, e.g. GoogleService-Info-Staging
   */

  googleServicePlistPath?: string;

  /**
   * Must be true if you wish to access user APIs on behalf of the user from
   * your own server
   */
  offlineAccess?: boolean;

  /**
   * Specifies a hosted domain restriction
   */
  hostedDomain?: string;

  /**
   * iOS ONLY.[iOS] The user's ID, or email address, to be prefilled in the authentication UI if possible.
   * https://developers.google.com/identity/sign-in/ios/api/interface_g_i_d_sign_in.html#a0a68c7504c31ab0b728432565f6e33fd
   */
  loginHint?: string;

  /**
   * ANDROID ONLY. If true, the granted server auth code can be exchanged for an access token and a refresh token.
   */
  forceCodeForRefreshToken?: boolean;

  /**
   * ANDROID ONLY. An account name that should be prioritized.
   */
  accountName?: string;
}
