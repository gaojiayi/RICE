interface ProxyStorage {
  getItem(key: string): any;
  setItem(Key: string, value: string): void;
  removeItem(key: string): void;
  clear(): void;
}

//sessionStorage operate
class sessionStorageProxy implements ProxyStorage {
  protected storage: ProxyStorage;

  constructor(storageModel: ProxyStorage) {
    this.storage = storageModel;
  }

  // 存
  public setItem(key: string, value: any): void {
    this.storage.setItem(key, JSON.stringify(value));
  }

  // 取
  public getItem(key: string): any {
    return JSON.parse(this.storage.getItem(key));
  }

  // 删
  public removeItem(key: string): void {
    this.storage.removeItem(key);
  }

  // 清空
  public clear(): void {
    this.storage.clear();
  }
}

//localStorage operate
class localStorageProxy extends sessionStorageProxy implements ProxyStorage {
  constructor(localStorage: ProxyStorage) {
    super(localStorage);
  }
}

// sessionStorage
// 临时存储，为每一个数据源维持一个存储区域，在浏览器打开期间存在，包括页面重新加载。

export const storageSession = new sessionStorageProxy(sessionStorage);

// localStorage
// 长期存储，与 sessionStorage 一样，但是浏览器关闭后，数据依然会一直存在。

export const storageLocal = new localStorageProxy(localStorage);
