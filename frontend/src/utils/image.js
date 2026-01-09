export function normalizePosterUrl(input) {
  if (!input) return ''
  const url = String(input).trim()
  if (!url) return ''

  if (url.startsWith('http://') || url.startsWith('https://')) return url
  if (url.startsWith('//')) return `https:${url}`

  // TMDb 常见：只存了 path（如 /abc.jpg）
  if (url.startsWith('/')) return `https://image.tmdb.org/t/p/w500${url}`

  return url
}

