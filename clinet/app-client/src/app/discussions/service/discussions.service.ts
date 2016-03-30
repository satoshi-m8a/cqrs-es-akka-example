import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {Discussion} from '../model/discussion';
import {Response} from 'angular2/http';
import {Config} from '../../../config';

@Injectable()
export class DiscussionsService {
    constructor(private http:Http, private config:Config) {
    }

    getDiscussions():Observable<Array<Discussion>> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + '/api/v1/discussions').map((res:Response)=> {
            console.log(res);
            return [new Discussion('id', 'title')];
        });
    }
}
