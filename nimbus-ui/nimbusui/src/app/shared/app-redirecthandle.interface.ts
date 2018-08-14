import { Serializable } from './serializable';

export class RedirectHandle implements Serializable<RedirectHandle, string> {
    token: string;
    commandUrl: string;

    deserialize( inJson ) {
        this.token = inJson.token;
        this.commandUrl = inJson.commandUrl;
        return this;
    }
}
