import { goto } from '$app/navigation';
import type { Page } from '@sveltejs/kit';

import type { components, paths } from '$lib/types/api/v1/schema';
import createClient from 'openapi-fetch';

import toastr from 'toastr';
import 'toastr/build/toastr.css';

toastr.options = {
  showDuration: 300,
  hideDuration: 300,
  timeOut: 3000,
  extendedTimeOut: 1000
};

class Rq {
  public member: components['schemas']['MemberDto'];

  constructor() {
    this.member = this.makeReactivityMember();
  }

  public effect(fn: () => void) {
    $effect(fn);
  }

  public isAdmin() {
    if (this.isLogout()) return false;

    return this.member.authorities.includes('ROLE_ADMIN');
  }

  public isAdmPage($page: Page<Record<string, string>>) {
    return $page.url.pathname.startsWith('/adm');
  }

  public isUsrPage($page: Page<Record<string, string>>) {
    return !this.isAdmPage($page);
  }

  // URL
  public goTo(url: string) {
    goto(url);
  }

  public replace(url: string) {
    goto(url, { replaceState: true });
  }

  public reload() {
    this.replace('/redirect?url=' + window.location.href);
  }

  // API END POINTS
  public apiEndPoints() {
    return createClient<paths>({
      baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
      credentials: 'include'
    });
  }

  // MSG, REDIRECT
  public msgAndRedirect(
    data: { msg: string } | undefined,
    error: { msg: string } | undefined,
    url: string,
    callback?: () => void
  ) {
    if (data) this.msgInfo(data.msg);
    if (error) this.msgError(error.msg);

    this.replace(url);

    if (callback) window.setTimeout(callback, 100);
  }

  public msgInfo(message: string) {
    toastr.info(message);
  }

  public msgError(message: string) {
    toastr.error(message);
  }

  // 인증
  // 이렇게 member 를 만들면 좋은 점이 있다.
  // member 의 값이 바뀌면, member 를 사용하는 모든 곳에서 자동으로 즉각 반영된다.
  public makeReactivityMember() {
    let id = $state(0);
    let name = $state('');
    let profileImgUrl = $state('');
    let createDate = $state('');
    let modifyDate = $state('');
    let authorities: string[] = $state([]);

    return {
      get id() {
        return id;
      },
      set id(value: number) {
        id = value;
      },
      get createDate() {
        return createDate;
      },
      set createDate(value: string) {
        createDate = value;
      },
      get modifyDate() {
        return modifyDate;
      },
      set modifyDate(value: string) {
        modifyDate = value;
      },
      get name() {
        return name;
      },
      set name(value: string) {
        name = value;
      },
      get profileImgUrl() {
        return profileImgUrl;
      },
      set profileImgUrl(value: string) {
        profileImgUrl = value;
      },
      get authorities() {
        return authorities;
      },
      set authorities(value: string[]) {
        authorities = value;
      }
    };
  }

  public setLogined(member: components['schemas']['MemberDto']) {
    Object.assign(this.member, member);
  }

  public setLogout() {
    this.member.id = 0;
    this.member.createDate = '';
    this.member.modifyDate = '';
    this.member.name = '';
    this.member.profileImgUrl = '';
    this.member.authorities = [];
  }

  public isLogin() {
    return this.member.id !== 0;
  }

  public isLogout() {
    return !this.isLogin();
  }

  public async initAuth() {
    const { data } = await this.apiEndPoints().GET('/api/v1/members/me');

    if (data) {
      this.setLogined(data.data.item);
    }
  }

  public async logoutAndRedirect(url: string) {
    await this.apiEndPoints().POST('/api/v1/members/logout');

    this.setLogout();
    this.replace(url);
  }

  // 글
  public async confirmAndDeletePost(
    post: components['schemas']['PostDto'],
    callback: string | Function
  ) {
    if (!window.confirm('삭제하시겠습니까?')) return;

    await this.apiEndPoints().DELETE('/api/v1/posts/{id}', {
      params: {
        path: {
          id: post.id
        }
      }
    });

    if (typeof callback === 'function') callback();
    else this.replace(callback);
  }

  public async goToTempPostEditPage() {
    const { data } = await this.apiEndPoints().POST('/api/v1/posts/temp');

    if (data) {
      this.msgInfo(data.msg);
      this.goTo(`/p/${data.data.item.id}/edit`);
    }
  }

  public async like(
    post: components['schemas']['PostDto'],
    callback: string | ((data: components['schemas']['RsDataLikeResponseBody']) => void)
  ) {
    const { data } = await this.apiEndPoints().POST('/api/v1/posts/{id}/like', {
      params: {
        path: {
          id: post.id
        }
      }
    });

    if (typeof callback === 'function') callback(data!);
    else this.replace(callback);
  }

  public async cancelLike(
    post: components['schemas']['PostDto'],
    callback: string | ((data: components['schemas']['RsDataCancelLikeResponseBody']) => void)
  ) {
    const { data } = await this.apiEndPoints().DELETE('/api/v1/posts/{id}/cancelLike', {
      params: {
        path: {
          id: post.id
        }
      }
    });

    if (typeof callback === 'function') callback(data!);
    else this.replace(callback);
  }

  public getGoogleLoginUrl() {
    return `${
      import.meta.env.VITE_CORE_API_BASE_URL
    }/member/socialLogin/google?redirectUrl=${encodeURIComponent(
      import.meta.env.VITE_CORE_FRONT_BASE_URL
    )}/member/socialLoginCallback?provierTypeCode=google`;
  }

  public goToCurrentPageWithNewParam(name: string, value: string) {
    // 현재 URL 객체 생성
    const currentUrl = new URL(window.location.href);
    // 쿼리 매개변수를 수정하기 위한 URLSearchParams 객체 생성
    const searchParams = currentUrl.searchParams;
    // 'page' 매개변수를 새 페이지 번호로 설정
    searchParams.set(name, value);
    this.goToCurrentPageWithNewQueryStr(searchParams.toString());
  }
  public goToCurrentPageWithNewQueryStr(query: string) {
    this.goTo(window.location.pathname + '?' + query);
  }
}

const rq = new Rq();

export default rq;