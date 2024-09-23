import { goto } from '$app/navigation';

import type { paths } from '$lib/types/api/v1/schema';
import createClient from 'openapi-fetch';

class Rq {
  public goTo(url: string) {
    goto(url);
  }

  public replace(url: string) {
    goto(url, { replaceState: true });
  }

  public apiEndPoints() {
    return createClient<paths>({
      baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
      credentials: 'include'
    });
  }

  public msgAndRedirect(
    data: { msg: string } | undefined,
    error: { msg: string } | undefined,
    url: string
  ) {
    if (data) this.msgInfo(data.msg);
    if (error) this.msgError(error.msg);

    this.replace(url);
  }

  public msgInfo(msg: string) {
    window.alert(msg);
  }

  public msgError(msg: string) {
    window.alert(msg);
  }
}

const rq = new Rq();

export default rq;