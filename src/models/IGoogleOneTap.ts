export interface IGoogleOneTap {
  /**
   * Returns a Promise that resolves with the current signed in user or rejects
   * if not signed in.
   */
  // function signInSilently(): Promise<User>;
  /**
   * Prompts the user to sign in with their Google account. Resolves with the
   * user if successful.
   */
  // function signIn(): Promise<IUser>;
  /**
   * Signs the user out.
   */
  // function signOut(): Promise<null>;
  /**
   * Save password after normal sign up / sign in
   */
  // function savePassword(userId: string, password: string): Promise<boolean>;
}
