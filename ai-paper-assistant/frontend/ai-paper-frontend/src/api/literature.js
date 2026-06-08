import api from './index'

export function upload(formData) {
  return api.post('/literature/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function list() {
  return api.get('/literature')
}

export function getById(id) {
  return api.get(`/literature/${id}`)
}

export function remove(id) {
  return api.delete(`/literature/${id}`)
}

export function generateSummary(id) {
  return api.post(`/literature/${id}/summary`)
}
