import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'e923223b-61b6-433e-99d0-3a047ca22b4e',
};

export const sampleWithPartialData: IAuthority = {
  name: '1fe7b8b3-7848-4c8b-9a01-aa2fc6c45ff8',
};

export const sampleWithFullData: IAuthority = {
  name: 'e78505be-93cd-4e24-aca4-6ed04348b8fe',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
