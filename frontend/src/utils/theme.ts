export type ThemeMode = 'light' | 'dark'

export const THEME_STORAGE_KEY = 'jobmate_theme_mode'

export function getStoredThemeMode(): ThemeMode {
  if (typeof localStorage === 'undefined') return 'light'
  const stored = localStorage.getItem(THEME_STORAGE_KEY)
  return stored === 'dark' ? 'dark' : 'light'
}

export function applyThemeMode(mode: ThemeMode) {
  const root = document.documentElement
  const body = document.body
  root.setAttribute('data-theme', mode)
  if (body) {
    body.setAttribute('data-theme', mode)
  }
  localStorage.setItem(THEME_STORAGE_KEY, mode)
}
