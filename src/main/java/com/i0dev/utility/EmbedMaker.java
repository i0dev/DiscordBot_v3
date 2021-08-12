package com.i0dev.utility;

import com.i0dev.object.EmbedColor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

@Builder
@Getter
@Setter
public class EmbedMaker {

    String title, content, footer, footerImg, image, thumbnail, authorName, authorURL, authorImg;

    User author, user;

    MessageEmbed.Field[] fields;

    TemporalAccessor timestamp;

    MessageEmbed.Field field;

    EmbedColor embedColor;

    public static MessageEmbed create(EmbedMaker builder) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);

        if (builder.getContent() != null)
            embedBuilder.setDescription(PlaceholderUtil.convert(builder.getContent(), builder));

        if (builder.getFooter() != null && builder.getFooterImg() == null)
            embedBuilder.setFooter(PlaceholderUtil.convert(builder.getFooter(), builder));

        if (builder.getFooter() != null && builder.getFooterImg() != null)
            embedBuilder.setFooter(PlaceholderUtil.convert(builder.getFooter(), builder), builder.getFooterImg());

        if (builder.getTitle() != null)
            embedBuilder.setTitle(PlaceholderUtil.convert(builder.getTitle(), builder));

        if (builder.getImage() != null)
            embedBuilder.setImage(builder.getImage());

        if (builder.getThumbnail() != null)
            embedBuilder.setThumbnail(builder.getThumbnail());

        embedBuilder.setAuthor(PlaceholderUtil.convert(builder.getAuthorName(), builder), builder.getAuthorURL(), builder.getAuthorImg());

        if (builder.getTimestamp() != null)
            embedBuilder.setTimestamp(builder.getTimestamp());

        if (builder.getFields() != null) {
            for (MessageEmbed.Field field : builder.getFields()) {
                MessageEmbed.Field newField = new MessageEmbed.Field(PlaceholderUtil.convert(field.getName(), builder), PlaceholderUtil.convert(field.getValue(), builder), field.isInline());
                embedBuilder.addField(newField);
            }
        }

        if (builder.getField() != null) {

            MessageEmbed.Field newField = new MessageEmbed.Field(PlaceholderUtil.convert(builder.getField().getName(), builder), PlaceholderUtil.convert(builder.getField().getValue(), builder), builder.getField().isInline());
            embedBuilder.addField(newField);

        }

        EmbedColor color = EmbedColor.NORMAL;
        if (builder.getEmbedColor() != null) color = builder.getEmbedColor();
        embedBuilder.setColor(Color.decode(color.getHexCode()));
        return embedBuilder.build();
    }

}


