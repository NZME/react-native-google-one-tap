export interface IUser {
  user: {
    id: string;
    name: string | null;
    familyName: string | null;
    givenName: string | null;
    password: string | null;
    photo: string | null;
  };
  idToken: string | null;
}
