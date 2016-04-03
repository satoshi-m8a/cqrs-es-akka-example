import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {Discussion} from '../model/discussion';
import {Response} from 'angular2/http';
import {Comment} from '../model/comment';
import {Config} from '../../config';
import {Headers} from 'angular2/http';

@Injectable()
export class DiscussionsService {
    constructor(private http:Http, private config:Config) {
    }

    getDiscussions():Observable<Array<Discussion>> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + '/api/v1/discussions').map((res:Response)=> {
            return res.json().map((item:any)=> {
                return new Discussion(item.id, item.title);
            });
        });
    }

    getDiscussion(id:string):Observable<Discussion> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + `/api/v1/discussions/${id}`).map((res:Response)=> {
            let item = res.json();
            return new Discussion(item.id, item.title);
        });
    }

    getComments(id:string):Observable<Array<Comment>> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + `/api/v1/discussions/${id}/comments`).map((res:Response)=> {
            console.log(res.json());
            return res.json().map((d:any)=> {
                return new Comment(d.id, d.text);
            });
        });
    }

    addComment(id:string, text:string):Observable<Comment> {
        let json = JSON.stringify({
            text: text
        });
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        //noinspection TypeScriptUnresolvedFunction
        return this.http.post(this.config.API_URL + `/api/v1/discussions/${id}/comments`, json, {
            headers: headers
        }).map((res:Response)=> {
            let item = res.json();
            return new Comment(item.id, item.text);
        });
    }
}
