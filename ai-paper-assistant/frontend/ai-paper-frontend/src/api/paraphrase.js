import api from './index'

export function detect(data) {
  return api.post('/paraphrase/detect', data)
}

export function rewrite(data) {
  return api.post('/paraphrase/rewrite', data)
}
