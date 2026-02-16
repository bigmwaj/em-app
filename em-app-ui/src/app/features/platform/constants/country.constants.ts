/**
 * Country constants for contact address forms
 */
export const COUNTRIES = [
  'Cameroun',
  'Burkina Faso',
  'Canada',
  'Cote d\'Ivoire'
] as const;

export type Country = typeof COUNTRIES[number];
