import api from './index'

export function format(data) {
  return api.post('/reference/format', data)
}

export function getStandards() {
  return api.get('/reference/standards')
}
