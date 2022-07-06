import { http } from "../utils/http";

interface ReponseType extends Promise<any> {
  list?: object[];
  total?: number;
}

export const getProcessorinfo = (data?: object): ReponseType => {
  return http.request("get", "/app/fetchProcessorInfo", { data });
};
