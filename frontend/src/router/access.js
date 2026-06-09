export function canAccessRole(role, acceptedRoles = []) {
  return acceptedRoles.includes((role || '').toLowerCase())
}

export function defaultRouteForRole(role) {
  return ['doctor', 'admin'].includes((role || '').toLowerCase()) ? '/admin' : '/'
}
