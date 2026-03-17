import Cookies from 'js-cookie'

const state = {
  openId: Cookies.get('openProjectId') ? Cookies.get('openProjectId') : '',
}

const mutations = {
  SET_OPEN_ID: (state,openId) => {
    Cookies.set('openProjectId', openId)
  }
}

const actions = {
  setOpenId({ commit },openId) {
    commit('SET_OPEN_ID',openId)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
