/**
 * Country constants for contact address forms
 */
export const COUNTRIES = ['Cameroun', 'Burkina Faso', 'Canada', 'Côte d\u2019Ivoire'] as const;

export type Country = (typeof COUNTRIES)[number];
